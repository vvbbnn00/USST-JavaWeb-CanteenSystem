"use client";
import {Avatar, Button, Chip, Image, Textarea, User} from "@nextui-org/react";

export default function ImageLayout({imageInfoList}) {
    if (!imageInfoList) {
        return <></>;
    }
    const imageCount = imageInfoList?.length;

    let containerClass = "";
    switch (imageCount) {
        case 1:
            containerClass = "grid grid-cols-1";
            break;
        case 2:
            containerClass = "grid grid-cols-2 gap-1.5";
            break;
        case 3:
            containerClass = "grid grid-cols-2 grid-rows-2 gap-1.5";
            break;
        case 4:
            containerClass = "grid grid-cols-2 grid-rows-2 gap-1.5";
            break;
        default:
            containerClass = "grid grid-cols-2 grid-rows-2 gap-1.5";
            break;
    }

    return <div className={"rounded-md overflow-hidden"}>
        <div className={containerClass}>
            {imageInfoList?.slice(0, 4).map((image, index) => (
                <div key={index}
                     className={`relative ${imageCount === 3 && index === 0 ? 'col-span-2' : ''} ${imageCount === 1 ? 'h-auto max-h-[500px]' : 'h-[200px]'}`}>
                    <Image
                        src={image?.x384Url}
                        alt={image.name}
                        radius={"none"}
                        className={"object-cover h-" + (imageCount === 1 ? '[500px]' : '[200px]') + " w-full"}
                        loading={"lazy"}
                    />
                </div>
            ))}
        </div>
    </div>;
}
